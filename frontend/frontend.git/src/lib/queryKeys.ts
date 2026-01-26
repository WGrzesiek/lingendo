import {DeckVisibility} from "@/features/dashboard-teacher/types";
import {DeckOwnerType} from "@/features/deck/types";

export const qk = {
  // ============================================
  // AUTH
  // ============================================
  auth: {
    all: ["auth"] as const,
    currentUser: () => ["current-user"] as const,
    me: () => ["auth", "me"] as const,
  },

  // ============================================
  // LEARNING
  // ============================================
  learning: {
    all: ["learning"] as const,
    nextFlashcard: (sessionId: string) =>
      ["learning", "session", sessionId, "nextFlashcard"] as const,
    headerProgress: (sessionId: string) =>
      ["learning", "session", sessionId, "headerProgress"] as const,
    nextFlashcardReview: (enrollmentId: string) =>
      ["learning", "enrollment", enrollmentId, "nextFlashcardReview"] as const,
    sessionCompleted: (sessionId: string) =>
      ["learning", "sessionCompleted", sessionId] as const,
  },

  // ============================================
  // SETTINGS
  // ============================================
  settings: {
    all: ["settings"] as const,
    profile: () => [...qk.settings.all, "profile"] as const,
  },

  // ============================================
  // DECK
  // ============================================
  deck: {
    all: ["decks"] as const,
    detail: (deckId: string) => ["deck", deckId] as const,
    detail1: (deckId: string) => ["deck-detail1", deckId] as const,
    details: (deckId: string) => ["deck-details", deckId] as const,
    flashcards: (deckId: string) =>
      ["deck-flashcards", "infinite", deckId] as const,
    statistics: (deckId: string) => ["deck-statistics", deckId] as const,
    userDecks: () => ["user-decks"] as const,
    iDecks: (page: number, size: number) => ["i-decks", page, size] as const,
    iDecksInfinite: () => ["i-decks", "infinite"] as const,
    // iDecksCreateInfinite: (filters?: unknown) =>
    //   ["i-decks-create", "infinite", filters] as const,
    iDecksCreateInfiniteRoot: () => ["deck", "createdByMe", "infinite"] as const,

    iDecksCreateInfinite: (args: {
      filters: {
        deckVisibility?: DeckVisibility[];
        owner?: DeckOwnerType[];
      };
      pageSize: number;
    }) => [...qk.deck.iDecksCreateInfiniteRoot(), args] as const,

    myDeckStats: (deckIds: string[]) => ["my-deck-stats", deckIds] as const,
    publicDecks: (page?: number, size?: number) =>
      ["publicDecks", page, size] as const,
  },

  // ============================================
  // DECK SHARE
  // ============================================
  deckShare: {
    all: ["deck-shares"] as const,
    deckShares: (deckId: string) => ["deck-shares", "deck", deckId] as const,
    myShares: () => ["deck-shares", "my"] as const,
    mySharesPaged: (page: number, size: number) =>
      ["deck-shares", "my", page, size] as const,
    sharedWithMe: () => ["deck-shares", "shared-with-me"] as const,
    sharedWithMePaged: (page: number, size: number) =>
      ["deck-shares", "shared-with-me", page, size] as const,
    hasAccess: (deckId: string) => ["deck-shares", "access", deckId] as const,
  },

  // ============================================
  // COURSE
  // ============================================
  course: {
    all: ["course"] as const,
    progress: (enrollmentId: string) =>
      ["course-progress", enrollmentId] as const,
    settings: (enrollmentId: string) =>
      ["course-settings", enrollmentId] as const,
    header: (enrollmentId: string) => ["course-header", enrollmentId] as const,
    words: (enrollmentId: string) =>
      ["courseWords", enrollmentId, "infinite"] as const,
    flashcardAnswersStats: (enrollmentId: string) =>
      ["flashcardAnswersStats", enrollmentId] as const,
  },

  // ============================================
  // REVIEW
  // ============================================
  review: {
    all: ["review"] as const,
    header: (enrollmentId: string) => ["review-header", enrollmentId] as const,
    words: (enrollmentId: string) =>
      ["reviewWords", enrollmentId, "infinite"] as const,
  },

  // ============================================
  // STATISTICS
  // ============================================
  statistics: {
    all: ["statistics"] as const,
    student: () => ["studentStatistics"] as const,
    activity: () => ["studentActivity"] as const,
    leaderboard: () => ["leaderBoardOverview"] as const,
  },

  // ============================================
  // FRIENDS
  // ============================================
  friends: {
    all: ["friends"] as const,
    list: () => ["friends", "list"] as const,
    listFiltered: (filters?: unknown) => ["friends", "list", filters] as const,
    allFriends: () => ["friends", "all"] as const,
    enriched: () => ["friends", "enriched"] as const,
    stats: () => ["friends", "stats"] as const,
    userStats: (userId: string) => ["friends", "userStats", userId] as const,
    check: (userId: string) => ["friends", "check", userId] as const,
    comparison: (friendId: string) =>
      ["friends", "comparison", friendId] as const,
    requests: () => ["friends", "requests"] as const,
    receivedRequests: () => ["friends", "requests", "received"] as const,
    sentRequests: () => ["friends", "requests", "sent"] as const,
    blocked: () => ["friends", "blocked"] as const,
    search: (query: string) => ["friends", "search", query] as const,
  },

  // ============================================
  // GROUPS
  // ============================================
  groups: {
    all: ["groups"] as const,
    list: () => ["groups", "list"] as const,
    detail: (groupId: string) => ["groups", "detail", groupId] as const,
    members: (groupId: string) =>
      ["groups", groupId, "members"] as const,
    stats: () => ["groupStats"] as const,
    withStats: () => ["groupStats", "list"] as const,
    dashboard: (groupId: string) =>
      ["groupStats", groupId, "dashboard"] as const,
    statsDetail: (groupId: string) => ["groupStats", groupId, "stats"] as const,
    topMembers: (groupId: string, limit?: number) =>
      ["groupStats", groupId, "topMembers", limit] as const,
    allMembers: (groupId: string) =>
      ["groupStats", groupId, "allMembers"] as const,
    courses: (groupId: string, limit?: number) =>
      ["groupStats", groupId, "courses", limit] as const,
    activity: (groupId: string, limit?: number) =>
      ["groupStats", groupId, "activity", limit] as const,
    leaderboard: (groupId: string, days?: number, limit?: number) =>
      ["groupStats", groupId, "leaderboard", days, limit] as const,
    progress: (groupId: string) => ["groupStats", groupId, "progress"] as const,
    progressByDeck: (groupId: string, deckId: string) =>
      ["groupStats", groupId, "progress", deckId] as const,
  },

  // ============================================
  // STUDENT GROUPS (Teacher panel)
  // ============================================
  studentGroups: {
    all: ["student-groups"] as const,
    groups: () => ["student-groups", "groups"] as const,
    groupsList: (includeArchived: boolean, page: number, size: number) =>
      [
        "student-groups",
        "groups",
        "list",
        { includeArchived, page, size },
      ] as const,
    groupDetail: (groupId: string) =>
      ["student-groups", "groups", "detail", groupId] as const,
    groupMembers: (groupId: string, page: number, size: number) =>
      ["student-groups", "groups", "members", groupId, { page, size }] as const,
    stats: () => ["student-groups", "stats"] as const,
    myGroups: () => ["student-groups", "my-groups"] as const,
    myGroupsList: (page: number, size: number) =>
      ["student-groups", "my-groups", "list", { page, size }] as const,
  },

  // ============================================
  // TEACHER STUDENT
  // ============================================
  teacherStudent: {
    all: ["teacher-student"] as const,
    students: () => ["teacher-student", "students"] as const,
    studentsList: (page: number, size: number) =>
      ["teacher-student", "students", "list", { page, size }] as const,
    topStudents: (limit: number) =>
      ["teacher-student", "students", "top", { limit }] as const,
    invitations: () => ["teacher-student", "invitations"] as const,
    invitationsList: (page: number, size: number) =>
      ["teacher-student", "invitations", "list", { page, size }] as const,
    invitationInfo: (code: string) =>
      ["teacher-student", "invitations", "info", code] as const,
    stats: () => ["teacher-student", "stats"] as const,
    statsDetails: () => ["teacher-student", "stats-details"] as const,
    activity: (limit: number) =>
      ["teacher-student", "activity", { limit }] as const,
    myTeachers: () => ["teacher-student", "my-teachers"] as const,
    myTeachersList: (page: number, size: number) =>
      ["teacher-student", "my-teachers", "list", { page, size }] as const,
  },

  // ============================================
  // TEACHER (Dashboard)
  // ============================================
  teacher: {
    students: () => ["teacher", "students"] as const,
    studentsList: (filters?: unknown, page?: number) =>
      ["teacher", "students", "list", filters, page] as const,
    studentDetail: (id: string) =>
      ["teacher", "students", "detail", id] as const,
    invitations: () => ["teacher", "invitations"] as const,
    courses: () => ["teacher", "courses"] as const,
    dashboardStats: () => ["teacher", "dashboard", "stats"] as const,
    activity: () => ["teacher", "dashboard", "activity"] as const,
  },

  // ============================================
  // MY TEACHERS (Student panel)
  // ============================================
  myTeachers: {
    all: ["my-teachers"] as const,
    list: (page?: number) => ["my-teachers", "list", page] as const,
    invitationInfo: (code: string) =>
      ["my-teachers", "invitation", code] as const,
  },

  // ============================================
  // COMMUNITY
  // ============================================
  community: {
    words: () => ["community-words"] as const,
  },
} as const;
