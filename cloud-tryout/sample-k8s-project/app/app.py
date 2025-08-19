from flask import Flask, jsonify
from prometheus_client import Counter, generate_latest, CollectorRegistry, CONTENT_TYPE_LATEST

app = Flask(__name__)

REQUEST_COUNT = Counter('sample_request_count', 'Total HTTP requests')

@app.route('/')
def hello():
    REQUEST_COUNT.inc()
    return jsonify({"message": "Hello from sample-k8s app!"})

@app.route('/metrics')
def metrics():
    # Expose default metrics
    data = generate_latest()
    return data, 200, {"Content-Type": CONTENT_TYPE_LATEST}

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8080)
