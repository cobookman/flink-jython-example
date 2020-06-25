import json
import some_dependency

def handler(event):
    try:
        event = json.loads(event)
    except:
        return json.dumps({ 'resp': '', 'event': event, 'error': 'failed to parse json argument'})
    if 'url' not in event or len(event['url']) == 0:
        return json.dumps({ 'resp': '', 'event': event })
    resp = some_dependency.get(event['url'])
    out = { 'resp': resp.text, 'event': event }
    return json.dumps(out)