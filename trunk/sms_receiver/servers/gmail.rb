require File.join(File.dirname(__FILE__), "../base")
require 'open_movilforum'
require 'process'

# CREDENTIALS FOR GMAIL ACCOUNT
user = 'test.openmovilforum@gmail.com'
pass = '12341234'

server = OpenMovilforum::SMS::Receiver::Gmail.new(user, pass)
server.add_observer(Process::Proxy)
server.start