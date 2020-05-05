// content of index.js
const http = require('http');
const port = 1234;

const requestHandler = (request, response) => {
    response.statusCode = 503;
    response.end();
}

const server = http.createServer(requestHandler);

server.listen(port, (err) => {
    if (err) {
        return console.log('something bad happened', err);
    }

    console.log(`server is listening on ${port}`);
})