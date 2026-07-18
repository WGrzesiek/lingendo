# statistics-service: pozostały rewrite SQL w repozytoriach (ClickHouse → PostgreSQL)

Schemat (`db/migration/V1__init.sql`), pom, config i profil k8s są już przepisane na Postgres:
tabele bazowe + tabele wymiarowe (PK) + **VIEWs** odtwarzające dawne materialized views
(agregacja-on-read). Zostaje przetłumaczyć **surowy SQL CH w 9 repozytoriach** (JdbcTemplate)
oraz **upserty w consumerach** dla tabel wymiarowych.

## Cheatsheet CH → PG

| ClickHouse | PostgreSQL |
| --- | --- |
| `... FROM t FINAL` | `... FROM t` (dedup realizuje PK + upsert, `FINAL` usunąć) |
| `dictGet('analytics.usernames_dict','username', id)` | `JOIN analytics.user_dim ud ON ud.user_id = id` → `ud.username` (lub podzapytanie) |
| `countDistinct(x)` | `count(DISTINCT x)` |
| `count()` | `count(*)` |
| `now() - INTERVAL 30 DAY` | `now() - INTERVAL '30 days'` |
| `today()` | `current_date` |
| `toDate(x)` | `(x AT TIME ZONE 'UTC')::date` (lub `x::date`) |
| `toStartOfMonth(x)` | `date_trunc('month', x)::date` |
| `toStartOfWeek(x)` | `date_trunc('week', x)::date` |
| `toYYYYMM(x)` | `to_char(x,'YYYYMM')` |
| `toYYYYMMDD(x)` | `to_char(x,'YYYYMMDD')` |
| `sumIf(v, cond)` | `sum(v) FILTER (WHERE cond)` |
| `countIf(cond)` | `count(*) FILTER (WHERE cond)` |
| `if(cond, a, b)` | `CASE WHEN cond THEN a ELSE b END` |
| `concat(a, b)` | `a || b` (lub `concat(a,b)` — działa w PG) |
| `toString(x)` | `x::text` |

## Upserty w consumerach (dawne ReplacingMergeTree → PK + ON CONFLICT)

Tabele wymiarowe mają teraz PK; INSERT musi być upsertem, żeby zachować „ostatni stan":
- `user_dim` (PK user_id) — `INSERT ... ON CONFLICT (user_id) DO UPDATE SET username=EXCLUDED.username, event_time=EXCLUDED.event_time`
- `teacher_students` (PK teacher_id, student_id) — ON CONFLICT DO UPDATE SET status, event_time
- `teacher_shared_decks` (PK teacher_id, deck_id)
- `user_friendships` (PK user_id, friend_id) — wstawiane obustronnie (patrz FriendshipStatsRepository)
- `groups` (PK group_id), `group_members` (PK group_id, student_id), `group_shared_decks` (PK group_id, deck_id)

`user_dim` było zasilane MV z `user_logins` — teraz consumer loginów powinien dodatkowo
upsertować `user_dim` (albo trigger na `user_logins`).

## Checklist repozytoriów (liczba zapytań SQL)

- [ ] DashboardRepository (12) — countDistinct, INTERVAL, toStartOfMonth/Week, sumIf, today()
- [ ] DeckRepository (3) — INSERT-y (proste) + threshold
- [ ] DeckEnrollmentRepository (11) — INSERT-y + odczyty
- [ ] FlashcardRepository (8) — INSERT-y + odczyty (threshold w Javie, OK)
- [ ] SessionRepository (10)
- [ ] UserRepository (5) — toYYYYMM, toYYYYMMDD, sum(points) z VIEW user_points_*
- [ ] FriendshipStatsRepository (22) — FINAL, dictGet, sumIf, toStartOfWeek, countDistinct
- [ ] TeacherDashboardRepository (46) — INSERT-y (upsert) + feed z VIEW teacher_student_activity
- [ ] GroupStatisticsRepository (41) — FINAL, dictGet, if(), toDate, toStartOfMonth, JOIN FINAL

## Uwagi

- INSERT-y do tabel bazowych (eventy) działają bez zmian (usuń ewentualne CH-typy).
- Odczyty z dawnych tabel MV (`user_activity`, `user_points_*`, `teacher_student_activity`,
  `friends_stats_daily`, `group_activity`, `group_leaderboard`, `leaderboard_snapshot`) →
  te nazwy to teraz VIEWs, `SELECT ... FROM <nazwa>` działa; usuń tylko CH-funkcje nakładane
  na wierzchu.
- Po rewritecie: test integracyjny na Neon (albo lokalny Postgres) — sprawdź że dashboardy
  zwracają sensowne dane. Bez tego ryzyko cichych błędów agregacji.
