FROM python:3

WORKDIR /app

RUN pip install pyrogram TgCrypto requests

COPY * /app/

CMD ["python", "bot.py"]
