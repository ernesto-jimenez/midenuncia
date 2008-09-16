require 'open_movilforum'
# Load rails app environment
RAILS_ROOT = File.dirname(__FILE__) + '/../../rails_app/'
require RAILS_ROOT + 'config/boot'
require 'environment'

module Process
  class Ok
    def self.run(user, address)
      user = User.find_by_phone(user)
      if user and user.pending_video
        video = user.pending_video
        video.address = address
        video.destroy if video.upload
      end
    end

    private :initialize
  end
end