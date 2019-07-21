'use strict';

let _socket = false;
let _clientId = crypto.getRandomValues(new Uint32Array(1))[0];
let _utf8decoder = new TextDecoder('utf-8');
let _utf8encoder = new TextEncoder('utf-8');

export const sendMessage = json => {
    json['senderId'] = _clientId;
    let buf = _utf8encoder.encode(JSON.stringify(json));
    _socket.send(buf);
};

const _socketMessageHandler = event => {
    try {
        let _data = _utf8decoder.decode(event['data']);
        let _oParsedJson = JSON.parse(_data);
        let {'senderId': _sSenderId, 'type': _sType, 'subject': _sSubject, 'payload': _oPayload} = _oParsedJson;
        console.log(_oParsedJson);
    } catch(error) {
        // Only interested in proper JSON formatted data, ignore anything else!
    }
};

const _socketOpenHandler = () => {
    sendMessage({subject: "ws.clients", type: "connection handshake", payload: {message: "hello everyone!"}});
};

const _socketErrorHandler = () => {
    if (_socket) {
        _socket.close();
    }
    _socket = false;
};

const _socketCloseHandler = () => {
    setTimeout(function() {
        _connect();
    }, 2000);
};

const _connect = () => {
    let _host = location.origin.replace(/^http/, 'ws');
    let _sUrl = _host + '/messaging/event';
    _socket = new WebSocket(_sUrl + "?X-messaging-client-id=" + _clientId);

    _socket.binaryType = 'arraybuffer';

    _socket.addEventListener('open', _socketOpenHandler);
    _socket.addEventListener('message', _socketMessageHandler);
    _socket.addEventListener('error', _socketErrorHandler);
    _socket.addEventListener('close', _socketCloseHandler);

};

_connect();