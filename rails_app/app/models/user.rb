class User < ActiveRecord::Base
  validates_uniqueness_of :phone
  validates_presence_of :phone
  validates_presence_of :code
  
  before_validation_on_create :regenearte_code
  
  has_one :pending_video
  
  def send_new_code
    regenearte_code
    self.save
    send_code
  end
  
  private
  def regenearte_code
    self.code = (rand * 100000).to_i.to_s.ljust(5, '0').to_i
  end
  
  def send_code
    msg = "Introduce tu codigo de acceso en videodenuncias. Tu codigo de acceso es: #{self.code}"
    RAILS_DEFAULT_LOGGER.error('[SMS] sending sms')
    SmsSender.send_sms(self.phone, msg) rescue @invalid_phone = true
    RAILS_DEFAULT_LOGGER.error("[SMS] ERROR SENDING SMS") if @invalid_phone
    RAILS_DEFAULT_LOGGER.error("[SMS] -> Code sent to #{self.phone}: #{self.code}")
  end
  
  validate :check_valid_phone
  def check_valid_phone
    if @invalid_phone
      self.errors.add(:phone, 'Invalid phone')
    end
  end
end
