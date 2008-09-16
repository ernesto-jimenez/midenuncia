require 'rubygems'
require 'youtube_g'
#uploader = YouTubeG::Upload::VideoUpload.new('erjicaomf', 'openmovilforum', 'AI39si7fuXjf7WUcpjbzmpLUZxM3TvcVtFY75OqiqdUsJzFR8_BwdmyKG7Vro54sRTkiERyuFfZs7PvxCASuUaMWD6czzQ0lrw')
#result = uploader.upload(File.open('/Users/hydrus/videos/output0.avi'), :title => 'test', :description => 'cool vid d00d', :mime_type => 'video/avi', :category => 'People', :latitude => '0.12', :longitude => '0.12', :keywords => %w[place:asdf user_id:1234 openmovilforum videodenuncias])

client = YouTubeG::Client.new
videos = client.videos_by(:user => 'erjicaomf')
require 'ruby-debug'; debugger
video = client.video_by('oqqlfQUv50I')
puts "fin"
