require File.join(File.dirname(__FILE__), "../base")
require 'open_movilforum'
require 'process'

# CREDENTIALS FOR POP3 SERVER
user = ''
pass = ''

server = OpenMovilforum::SMS::Receiver::POP3.new(user, pass)
server.add_observer(Process::Proxy)
server.start