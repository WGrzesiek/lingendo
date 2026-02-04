// Inicjalizacja MongoDB dla vocabulary-read-service
// Skrypt wykonywany przy pierwszym uruchomieniu kontenera

db = db.getSiblingDB("vocabulary-read");

db.createUser({
  user: "User",
  pwd: "password",
  roles: [{ role: "readWrite", db: "vocabulary-read" }],
});

db.createCollection("vocabularies");

db.vocabularies.createIndex({ deckId: 1 });
db.vocabularies.createIndex({ userId: 1 });
db.vocabularies.createIndex({ createdAt: -1 });

print("MongoDB: Baza vocabulary-read zainicjalizowana pomyślnie!");
