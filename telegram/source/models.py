import sqlite3, requests, json

domain = "https://karyab-bahman-ahmadi.vercel.app"

class registration:
	def __init__(self):
		self.db = DB("users.db")

		# TUID : Telegram User's ID (telegram user ids is an integer but i saved that in string)
		# UUID : Karyab User's ID (Karyab user ids is a string)
		self.db.makeStorage("[TUID] NVARCHAR(160)  NOT NULL, [UUID] NVARCHAR(32)  NOT NULL")

	def getUUID(self, TUID:str):
		try:
			result = self.db.searchStorage("UUID", "TUID", TUID)[0]
		except IndexError:
			result = [[]]
		return result[0]

	def getTUID(self, UUID:str):
		result = self.db.searchStorage("TUID", "UUID", UUID)[0]
		return result[0]

	def saveUser(self, TUID, UUID):
		if len(registration().getUUID(TUID)) != 0: self.db.editStorage(f'UUID = "{UUID}"', f'TUID = "{TUID}"')
		else: self.db.writeStorage((TUID,UUID))

	def getAll(self, attr="*"): return [i[0] for i in self.db.readStorage(attr)]

	def getUser(self, UUID:str):
		return json.loads(requests.get(f"{domain}/api/getUser?UUID={UUID}").text)

class DB:
	def __init__(self, db):
		self.con = sqlite3.connect(db)
		self.cur = self.con.cursor()

	def makeStorage(self, cols):
		self.cur.execute(f"CREATE TABLE IF NOT EXISTS [storage]({cols});")
		self.con.commit()

	def writeStorage(self, queries):
		fields = str("?,"*len(queries))[:-1]
		self.cur.execute(f"INSERT INTO storage VALUES({fields});", queries)
		self.con.commit()

	def searchStorage(self, target, key, value):
		self.cur.execute(f"SELECT {target} FROM storage WHERE \"{key}\"=\"{value}\";")
		result = self.cur.fetchall()
		return result

	def readStorage(self, attr="*"):
		self.cur.execute(f"SELECT {attr} FROM storage;")
		result = self.cur.fetchall()
		return result

	def editStorage(self, changes:str, findBy:str):
		self.cur.execute(f"UPDATE storage SET {changes} WHERE {findBy};")
		self.con.commit()

	def closeStorage(self):
		self.con.close()
