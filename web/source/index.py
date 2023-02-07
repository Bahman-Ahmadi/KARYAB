from flask import Flask, render_template, request, redirect, jsonify
from models import registration, urls
from hashlib import sha256
from requests import get
from json import loads, dumps

app = Flask(__name__)

@app.route("/")
def index(): return render_template("register.html")

@app.route("/dashboard")
def dashboard():
	return render_template("dashboard.html", jobs=[])

@app.route("/loadDashboard")
def loadDashboard():
	jobs = getJobs(request.args.get("UUID"))
	return render_template("dashboard.html", jobs=jobs)
	
@app.route("/about")
def about(): return render_template("about.html")

@app.route("/register")
def register():
	try:
		print (registration().getAll())
		getData = request.args.get
		MODE = getData("mode")
		hashedPassword = sha256(getData("password").encode()).hexdigest()
		if MODE == "signup" :
			if not getData("email") in registration().getAll("email"):
				registration().newUser(
					name     = getData("name"),
					email    = getData("email"),
					password = hashedPassword,
					age      = int(getData("age")),
					skills   = getData("skills"),
					UUID     = registration().makeUUID()
				)
				return "حساب کاربری شما باموفقیت ساخته شد"
			else :
				return "این ایمیل قبلا استفاده شده است"
		else :
			# if MODE is not signup, so that's login.
			if getData("email") in registration().getAll("email") and hashedPassword in registration().getAll("password"):
				thisUser = registration().getUser(getData("email"))
				if thisUser.get("password") == hashedPassword:
					response = jsonify(thisUser)
					response.headers.add('Access-Control-Allow-Origin', '*')
					return response

				else : return "رمزعبور نادرست است"
			else :
				return "حسابی با این اطلاعات وجود ندارد"
	except AttributeError:
		return redirect("/")

@app.route("/settings")
def settings(): return render_template("settings.html", sites=list(urls.keys()))

@app.route("/editSite")
def editSite():
	try:
		getData = request.args.get

		user = registration().getUserByUUID(getData("UUID"))
		lastNonAllowedSites = [i for i in user.get("nonAllowedSites").replace("'","").replace("[","").replace("]","").split(",") if i != ""]
		nonAllowedSite = getData("site")

		if nonAllowedSite in lastNonAllowedSites :
			lastNonAllowedSites.remove(nonAllowedSite)
		else :
			lastNonAllowedSites.append(nonAllowedSite)

		registration().editUser(getData("UUID"), {"nonAllowedSites": ",".join(lastNonAllowedSites)})
		response = jsonify(registration().getUserByUUID(getData("UUID")))
		response.headers.add('Access-Control-Allow-Origin', '*')
		return response

	except Exception as e:
		print(e)
		return "Error"

@app.route("/api/getUser")
def getUserAPI():
	try:
		response = jsonify(status="ok", response=registration().getUserByUUID(request.args.get("UUID")))
	except Exception as e:
		response = jsonify(status="error",error=str(e))

	response.headers.add('Access-Control-Allow-Origin', '*')
	return response

@app.route("/api/getJobs")
def getJobsView():
	response = jsonify(jobs = getJobs(request.args.get("UUID")))
	response.headers.add('Access-Control-Allow-Origin', '*')
	return response

@app.route("/api/getJsonJobs")
def getJsonJobs():
	return str(loads(get(f"https://karyab-4d4bojmu2-bahman-ahmadi.vercel.app/api/getJobs?UUID={request.args.get('UUID')}").text))
	#return str(loads(get(f"http://localhost:5000/api/getJobs?UUID={request.args.get('UUID')}").text))

@app.errorhandler(404)
def notFound(error): return render_template("404.html"), 404

def getJobs(UUID):
	user = registration().getUserByUUID(UUID)
	jobs = []
	try:
		for site in urls.keys() :
			if not site in user["nonAllowedSites"] :
				searchedJobs = urls[site](" ".join(user["skills"].split(",")))
				for job in searchedJobs :
					jobs.append(job)
	except:
		jobs.append({
			"title": "خطا",
			"description": "متأسفیم، یک مشکل از سمت ما وجود دارد و در صورتی که آن را به ما گزارش دهید در اسرع وقت آن را برطرف می‌کنیم.",
			"link": domain+"/about" #for contacting
		})

	return jobs
