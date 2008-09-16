require 'open_movilforum'
# Load rails app environment
RAILS_ROOT = File.dirname(__FILE__) + '/../../rails_app/'
require RAILS_ROOT + 'config/boot'
require 'environment'

module Process
  class Ok
    def self.run(phone, address)
      user = User.find_by_phone(phone)
      if user and user.pending_video
        puts "User #{phone} wants to confirm a video upload"
        video = user.pending_video
        puts "Geocoding video..." unless address.strip == ""
        video.address = address
        puts "Uploading video to YouTube..."
        video.destroy if video.upload
        puts "Video uploaded"
      else
        puts "No pending videos from user #{phone}"
        puts "Sending instructions..."
        SmsSender.send_sms(phone, "No tienes ningun video pendiente de confirmacion")
        puts "SMS sent"
      end
    end

    private :initialize
  end
end