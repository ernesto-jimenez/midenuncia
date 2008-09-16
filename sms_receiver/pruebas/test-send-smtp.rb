require 'net/smtp' 

from = 'tests@testing.com'
to = 'test.openmovilforum@gmail.com'
Net::SMTP.start('localhost', 25) do |smtp|
  smtp.open_message_stream(from, [to]) do |f|
    f.puts "From: #{from}"
    f.puts "To: #{to}"
    f.puts 'Subject: test message'
    f.puts
    f.puts "Movil: erjica@gmail.com"
    f.puts "Texto: ocio restaurantes baratos: pintor lorenzo casanova 43b 4b"
  end
end