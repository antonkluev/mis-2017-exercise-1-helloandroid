


const http = require('http')

const server = http.createServer((request, response) => {
  response.end('Hello World!')
}).listen(8000, console.log)

console.log('server is running...')
