require 'hpricot'

class Video
  attr_reader :thumbnail_url, :video_url, :tags, :lat, :lng, :uid, :title, :description
  def self.all
    doc = self.fetch_feed
    (doc/"//channel/item").to_a.collect! {|v| Video.new(v) }
  end
  
  def geotagged?
    return(not self.lat.blank? and not self.lng.blank?)
  end
  
  def embed_html(width="425", height="344")
    "<object width=\"#{width}\" height=\"#{height}\"><param name=\"movie\" value=\"#{self.video_url}&hl=en&fs=1\"></param><param name=\"allowFullScreen\" value=\"true\"></param><embed src=\"#{self.video_url}&hl=en&fs=1\" type=\"application/x-shockwave-flash\" allowfullscreen=\"true\" width=\"#{width}\" height=\"#{height}\"></embed></object>"
  end
  
  private
  def self.feed_url
    "http://www.youtube.com/ut_rss?type=username&arg=#{PendingVideo::YOUTUBE_API[:user]}"
  end
  
  def self.fetch_feed
    return open(self.feed_url) { |f| Hpricot(f) }
  end
  
  def initialize(item)
    @description = (item/"//description").first.innerHTML.match(/<p>([^<]*)<\/p>/)[1].strip
    @thumbnail_url = (item/"//media:thumbnail").first['url']
    @video_url = (item/"//enclosure").first['url']
    @title = (item/"//title").first.innerHTML
    @tags = (item/"//media:category[@label='Tags']").first.innerHTML.split(' ')
    @lat = get_machine_tag('lat')
    @lng = get_machine_tag('lng')
    @uid = get_machine_tag('uid')
  end
  
  def get_machine_tag(mtag)
    (@tags.grep(/#{mtag}:/).first || "").sub(/#{mtag}:/, '')
  end
end