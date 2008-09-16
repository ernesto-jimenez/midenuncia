require 'google/static_base'
require 'json'

module Google
  class Geocoding < StaticBase
    attr_accessor :default_params
    URL = 'http://maps.google.com/maps/geo'
    
    DEFAULT_PARAMS = {:key => 'ABQIAAAAnYifODmURuREAUoO1JrZeBQgpuSXudThF8vciWXWA0WZzsNHsxQcusunz6aT2Pw8fTc_PbP7cYq7uQ'}
    FORCED_PARAMS = {:output => 'txt'}
  
    def self.build_url(params={})
      params = DEFAULT_PARAMS.merge(params).merge(FORCED_PARAMS)
      super(params)
    end
    
    
    # Returns an array [latitude, longitude]
    def self.geocode(query)
      reply = self.fetch_url(self.build_url(:q => query))
      coordinates = JSON.parse(reply)['Placemark'].first['Point']['coordinates']
      return coordinates[1], coordinates[0]
    end
  end
end