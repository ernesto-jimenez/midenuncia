module VideosHelper
  def map_url(element)
    Google::GMapsStatic.build_url(:markers => {:red => [element.lat, element.lng]}, :size => '300x300', :maptype => 'web', :format => 'png32', :key => maps_api_key)
  end
end
