from pyrogram import Client, filters
from pyrogram.types import Message, ReplyKeyboardMarkup, InlineKeyboardMarkup, InlineKeyboardButton
from models import registration, domain
from requests import get
from json import loads

bot = Client("bot",config_file="config.ini")

@bot.on_message(filters.private)
def pv_callback(client, m:Message):
	if m.text == "/start":
		bot.send_message(m.chat.id, f"""
سلام دوست من 😃👋
من کاریار تو هستم و برای کمک بهت در جهت پیدا کردن شغل مناسب در کنارت هستم 
اگه آماده‌ای که شروع کنیم، کافیه در سایت <a href="{domain}">ثبت‌نام</a> کنی و سپس به قسمت <a href="{domain}/settings">تنظیمات</a> حساب کاربری وارد بشی و شناسهٔ‌کاربریت رو واسم به این صورت ارسال کنی:
/register <pre>EXAMPLE</pre>
البته قبل از ارسال، کافیه بجای EXAMPLE شناسهٔ کاربریت رو جایگزین کنی. 😄
موفق باشی 🌺
""", parse_mode="html", reply_to_message_id=m.message_id)

	elif m.text.startswith("/register"):
		user = registration().getUser(m.text[10:])
		registration().saveUser(m.chat.id, m.text[10:])
		if user["status"] == "ok": bot.send_message(m.chat.id, "با موفقیت به حساب‌کاربریتان وارد شدید ✅\n- حالا هروقت خواستی شغل های مناسبت رو برات ارسال کنم، کافیه دستور /jobs رو ارسال کنی 🙂💼", reply_to_message_id=m.message_id)
		else : bot.send_message(m.chat.id, "متأسفم.. نتونستم اطلاعات رو پیدا کنم 😕\n- مطمئنی شناسه رو درست وارد کردی ؟", reply_to_message_id=m.message_id)

	elif m.text.startswith("/jobs"):
		if str(m.chat.id) in registration().getAll(attr="TUID"):
			UUID = registration().getUUID(str(m.chat.id))
			jobs = loads(get(f"{domain}/api/getJobs?UUID={UUID}").text)
			try: jobs = loads(jobs)
			except: ...
			for job in jobs.get("jobs"):
				bot.send_message(m.chat.id, "<b>"+job["title"]+"</b>\n"+job["description"], parse_mode='html',reply_to_message_id=m.message_id,reply_markup=InlineKeyboardMarkup([[InlineKeyboardButton(text= "مشاهده",url=job["link"])]]))

		else :
			bot.send_message(m.chat.id, "دوست عزیز شما به حساب‌کاربری خود وارد نشده‌اید.\nبا ارسال /start می‌توانید درمورد نحوهٔ ورود به حساب اطلاع پیدا کنید.", reply_to_message_id=m.message_id)

bot.run()
