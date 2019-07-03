'use strict';

let _socket = false;
let _clientHandlerId = null;
let _utf8decoder = new TextDecoder('utf-8');
let _utf8encoder = new TextEncoder('utf-8');

const _socketMessageHandler = event => {
    try {
        let _data = _utf8decoder.decode(event['data']);
        let _oParsedJson = JSON.parse(_data);

    } catch(error) {
        // Only interested in proper JSON formatted data, ignore anything else!
    }
};

const _socketOpenHandler = () => {

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
    _socket = new WebSocket(_sUrl);

    _socket.binaryType = 'arraybuffer';

    _socket.addEventListener('open', _socketOpenHandler);
    _socket.addEventListener('message', _socketMessageHandler);
    _socket.addEventListener('error', _socketErrorHandler);
    _socket.addEventListener('close', _socketCloseHandler);

    _socket.send()
};

_connect();