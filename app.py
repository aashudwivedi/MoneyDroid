#!flask/bin/python
from flask import Flask, jsonify, abort, request
from flask import Response
import json

app = Flask(__name__)

root = '/moneydroid/api/v1.0'

transactions = [
    {
        'id': 1,
        'title' : u'House Rent',
        'description' : u'house rent for the month of may',
        'amount' : 4500,
        'currency' : 'rs',
    },
    {
        'id' : 2,
        'title' : u'Table',
        'description' : u'a nice wide study table',
        'amount' : 2000,
        'currency' : 'rs',
        'done': False
    }
]

@app.route(root + '/transactions', methods = ['GET'])
def get_transactions():
    # return jsonify( results =  transactions)
    return Response(json.dumps(transactions),  mimetype='application/json')

@app.route(root + '/transactions/<int:transaction_id>', methods = ['GET'])
def get_transaction(transaction_id):
    task = filter(lambda t: t['id'] == transaction_id, transactions)
    if len(task) == 0:
        abort(404)
    return jsonify( { 'task': transactions[0] } )

@app.errorhandler(404)
def not_found(error):
    return make_response(jsonify( { 'error': 'Not found' } ), 404)

@app.route(root + '/tasks', methods = ['POST'])
def create_transaction():
    if not request.json or not 'title' in request.json:
        abort(400)
    transactions = {
        'id': transactions[-1]['id'] + 1,
        'title': request.json['title'],
        'description' : request.json.get('description', ""),
        'done': False
    }
    tasks.append(transactions)
    return jsonify( { 'task': transaction } ), 201



if __name__ == '__main__':
    app.run(debug = True, host= '0.0.0.0')
