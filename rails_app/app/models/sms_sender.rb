class SmsSender
  def self.send_sms(phone, msg)
    sender = OpenMovilforum::SMS::Sender.new(TELEFONO_ENVIO, "1509")
    sender.send(phone, msg)
  end
end

class MmsSender
  def self.send_mms(phone, msg, attach)
    sender = OpenMovilForum::MMS::Sender::Movistar.send(phone, "MiDenuncia", attach, msg)
  end
end