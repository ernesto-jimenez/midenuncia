require 'open_movilforum/mms'
require 'google'
require 'geonames'

#lat1 = "38.385199"
#long1 = "-0.514727"
lat1 = 38.345278
long1 = -0.483056

#finder = GeoNames::WikiFinder.new(lat1, long1, 10)
finder = GeoNames::WikiFinder.new(38.345278, -0.483056, 10)
puts finder.build_url

#map = Google::GMapsStatic.new([lat1 + "," + long1, "38.39751,-0.503311"])
#puts map.build_url

#sender = OpenMovilforum::MMS::MMSSender.new("test.openmovilforum", "12341234")
#msg = OpenMovilforum::MMS::Message.new("ladybenko@gmail.com", "Mapa", map.build_url, "¡Weeee~~~!")
#sender.send(msg)
