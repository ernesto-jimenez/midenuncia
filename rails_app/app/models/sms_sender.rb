class SmsSender
  def self.send_sms(phone, msg)
    sender = OpenMovilforum::SMS::Sender.new(TELEFONO_ENVIO, "1509")
    sender.send(phone, msg)
  end
end