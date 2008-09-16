require 'open_movilforum'
# también se puede hacer: require 'open_movilforum/sms/receiver/gmail'
# así no cargaríamos todas las libs de OMF
require 'process'

server = OpenMovilforum::SMS::Receiver::SMTP.new
server.add_observer(Process::Proxy)
server.start