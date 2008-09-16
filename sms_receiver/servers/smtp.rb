require File.join(File.dirname(__FILE__), "../base")
require 'open_movilforum'
require 'process'

server = OpenMovilforum::SMS::Receiver::SMTP.new
server.add_observer(Process::Proxy)
server.start