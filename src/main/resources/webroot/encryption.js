// PBKDF2 HMAC-256
const PBKDF2WithHmacSHA256 = (password, salt, iterations, mode) =>
    crypto.subtle.importKey("raw", password, {name: "PBKDF2"}, false, ["deriveKey"])
        .then(baseKey => crypto.subtle.deriveKey({name: "PBKDF2", salt, iterations, hash: {name: "SHA-256"}, }, baseKey, {name: mode, length: 256}, true, ["encrypt", "decrypt"]))
        .then(key => crypto.subtle.exportKey("raw", key));

const PBKDF2WithHmacSHA512 = (password, salt, iterations, mode) =>
    crypto.subtle.importKey("raw", password, {name: "PBKDF2"}, false, ["deriveKey"])
        .then(baseKey => crypto.subtle.deriveKey({name: "PBKDF2", salt, iterations, hash: {name: "SHA-512"}, }, baseKey, {name: mode, length: 256}, true, ["encrypt", "decrypt"]))
        .then(key => crypto.subtle.exportKey("raw", key));

// Encrypt
const encrypt = (data, key, iv, mode) =>
    crypto.subtle.importKey("raw", key, {name: mode}, true, ["encrypt", "decrypt"])
        .then(bufKey => crypto.subtle.encrypt({name: mode, iv}, bufKey, data));

// Decrypt
const decrypt = (data, key, iv, mode) =>
    crypto.subtle.importKey("raw", key, {name: mode}, true, ["encrypt", "decrypt"])
        .then(bufKey => crypto.subtle.decrypt({name: mode, iv}, bufKey, data));


// Utils
const hexToBuf = hex => {
    for (var bytes = [], c = 0; c < hex.length; c += 2)
        bytes.push(parseInt(hex.substr(c, 2), 16));
    return new Uint8Array(bytes);
};

const bufToHex = buf => {
    var byteArray = new Uint8Array(buf);
    var hexString = "";
    var nextHexByte;

    for (var i=0; i<byteArray.byteLength; i++) {
        nextHexByte = byteArray[i].toString(16);
        if (nextHexByte.length < 2) {
            nextHexByte = "0" + nextHexByte;
        }
        hexString += nextHexByte;
    }
    return hexString;
};

const strToBuf = str => (new TextEncoder().encode(str));
const bufToStr = str => (new TextDecoder().decode(str));

const strToB64 = str => btoa(encodeURIComponent(str).replace(/%([0-9A-F]{2})/g, (match, p1) => String.fromCharCode('0x' + p1)));
const b64ToStr = str => decodeURIComponent(Array.prototype.map.call(atob(str), c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)).join(''));

// const bufToB64 = buf => btoa(Array.prototype.map.call(buf, ch => String.fromCharCode(ch)).join(''));
const bufToB64 = buf => btoa(String.fromCharCode(...new Uint8Array(buf)))
const b64ToBuf = b64 => {
    const binstr = atob(b64),
        buf = new Uint8Array(binstr.length);
    Array.prototype.forEach.call(binstr, (ch, i) => {
        buf[i] = ch.charCodeAt(0);
    });
    return buf;
}