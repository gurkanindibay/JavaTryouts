FROM python:3.11-slim

WORKDIR /app

# system deps for manylinux wheels
RUN apt-get update && apt-get install -y --no-install-recommends \
    build-essential \
    && rm -rf /var/lib/apt/lists/*

COPY app/requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt
# copy application files into /app (copy contents, not the parent directory)
COPY app/ .
WORKDIR /app

EXPOSE 8080

# Use gunicorn for a production-like server
CMD ["gunicorn", "-w", "4", "-b", "0.0.0.0:8080", "app:app"]
