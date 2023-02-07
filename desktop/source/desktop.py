import sys
from PyQt5.QtCore import *
from PyQt5.QtGui import *
from PyQt5.QtWidgets import *
from requests import get

domain = "https://karyab-bahman-ahmadi.vercel.app"
UUID = open("UUID.txt", "r").read().strip()
jobs = []

def window():
	app = QApplication(sys.argv)
	app.setStyleSheet(open("styles/style.qss").read())
	win = QWidget()
	new = QWidget(win)
	layout = QFormLayout()

	if UUID == "":
		UUIDField = QLineEdit()
		UUIDField.setFont(QFont("Arial",18))
		UUIDField.setMaxLength(32)
		UUIDField.textChanged.connect(whenEditingUUID)
		
		check = QPushButton()
		check.setText("بررسی")
		check.clicked.connect(whenCheckingUUID)
	
		layout.addRow("UUID", UUIDField)
		layout.addRow(check)
	else :
		global jobs
		jobs = get(f"{domain}/api/getJobs?UUID={UUID}").json().get("jobs")
		listWidget = QListWidget()
		listWidget.itemClicked.connect(selected)
		for job in jobs:
			label = QListWidgetItem(job["title"]+"\n"+job["description"])
			label.setTextAlignment(Qt.AlignRight)
			label.setIcon(QIcon("arrow.png"))
			listWidget.addItem(label)
		layout.addRow(listWidget)

	win.setLayout(layout)
	win.setWindowTitle("KarYar")
	win.show()

	sys.exit(app.exec_())

def whenCheckingUUID():
	msg = QMessageBox()
	msg.setIcon(QMessageBox.Information)
	response = get(f"{domain}/api/getUser?UUID={UUID}").json()
	if response["status"] == "ok":
		msg.setText("با موفقیت به حساب‌کاربری خود وارد شدید\nجهت دریافت مشاغل برنامه را بسته\nو سپس باز کنید.")
	else :
		msg.setText("عملیات موفقیت آمیز نبود.\nاز صحیح بودن شناسهٔ وارد شده، اطمینان حاصل کنید.")

	msg.setWindowTitle("هشدار")
	msg.setStandardButtons(QMessageBox.Ok)
	retval = msg.exec_()

def whenEditingUUID(text):
	global UUID
	open("UUID.txt", "w").write(text)
	UUID = open("UUID.txt", "r").read().strip()

def selected(item):
	import webbrowser
	selectableItems = [i["title"]+"\n"+i["description"] for i in jobs]
	links = [i["link"] for i in jobs]
	webbrowser.open(links[selectableItems.index(item.text())])

if __name__ == '__main__': window()