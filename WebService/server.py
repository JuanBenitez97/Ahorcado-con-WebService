from flask import Flask
from flask import jsonify
import palabra


app = Flask(__name__)


@app.route("/")
def inicio():
	return "Funciona"


@app.route("/api")
def apiRest():
	var = palabra.devolver_palabra()
	return jsonify(var)


app.run("localhost", 8080)