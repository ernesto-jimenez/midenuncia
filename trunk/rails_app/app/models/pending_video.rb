require 'youtube_g'

class PendingVideo < ActiveRecord::Base
  YOUTUBE_API = {
    :user => 'erjicaomf',
    :pass => 'openmovilforum',
    :dev_key => 'AI39si7fuXjf7WUcpjbzmpLUZxM3TvcVtFY75OqiqdUsJzFR8_BwdmyKG7Vro54sRTkiERyuFfZs7PvxCASuUaMWD6czzQ0lrw'
  }
  
  SAVE_VIDEOS_IN = ":rails_root/public/:class/:id.avi"
  
  belongs_to :user
  
  has_attached_file :video, :url => SAVE_VIDEOS_IN, :path => SAVE_VIDEOS_IN
  validates_attachment_presence :video
  
  validates_presence_of :user_id
  validates_associated :user
  
  def upload
    RAILS_DEFAULT_LOGGER.debug("[YouTube] Uploading video #{self.video.path}")
    uploader = YouTubeG::Upload::VideoUpload.new(YOUTUBE_API[:user], YOUTUBE_API[:pass], YOUTUBE_API[:dev_key])
    result = uploader.upload(File.open(self.video.path),
      :title => self.title,
      :description => self.description,
      :mime_type => 'video/avi',
      :category => 'People',
      :keywords => self.youtube_keywords)
    begin
      SmsSender.send_sms(self.user.phone, "Videodenuncia publicada!") unless result.blank?
    rescue
    end
    RAILS_DEFAULT_LOGGER.debug("[YouTube] Video uploaded with ID #{result}")
  end
  
  def address=(value)
    if value.blank?
      self.lat = self.lng = nil
    else
      self.lat, self.lng = Google::Geocoding.geocode(value) 
    end
    super(value)
  end
  
  def geotagged?
    return(not self.lat.blank? and not self.lng.blank?)
  end
  
  def youtube_keywords
    keywords = []
    keywords << "lat:#{self.lat}" unless self.lat.blank?
    keywords << "lng:#{self.lng}" unless self.lng.blank?
    keywords << "uid:#{self.user_id}"
    keywords << "videodenuncia"
    keywords << "openmovilforum"
    keywords << "erjicaomf"
    return keywords
  end
  
  def description
    desc = self.title
    desc << " Grabada en #{self.address}" unless self.address.blank?
    desc << " (#{self.lat},#{self.lng})" unless self.lat.blank?
    return desc
  end
  
  def title
    "Videodenuncia ##{self.id}"
  end
  
  private
  after_create :destroy_previous_pending_videos
  def destroy_previous_pending_videos
    PendingVideo.destroy_all(['user_id = ? and id <> ?', self.user_id, self.id])
  end
end
