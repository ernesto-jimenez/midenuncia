require 'google/static_base'

module Google
  class GMapsStatic < StaticBase
    URL = 'http://maps.google.com/staticmap'
    DEFAULT_PARAMS = {
      :key => 'ABQIAAAAnYifODmURuREAUoO1JrZeBQgpuSXudThF8vciWXWA0WZzsNHsxQcusunz6aT2Pw8fTc_PbP7cYq7uQ',
      :maptype => 'mobile',
      :size => '512x512',
      :format => 'gif'
    }
    
    def self.build_url(params={})
      params = set_params(DEFAULT_PARAMS.merge(params))
      return super(params)
    end
    
    def self.each_with_character(list, &block)
      character = "a"
      list.each do |element|
        yield(character, element)
        character = character.succ
      end
    end
    
    private
    # Serializes markers datastructure into an string query
    def self.get_markers(markers)
      raise ArgumentError, "Markers params should be a Hash, was #{markers.inspect}" unless markers.kind_of?(Hash)
      points = []
      markers.each do |type, value|
        case type
        when :characters
          list = {}
          each_with_character(value) do |char, value|
            list["red#{char}"] = value
          end
          points << get_markers(list)
        else
          points << [get_coordinates(value), type].join(',')
        end
      end
      return points.join('|')
    end
    
    # Checks all params are valid a converts data structures to strings
    def self.set_params(params={})
      params = params.clone
      if params[:markers]
        params[:markers] = get_markers(params[:markers])
      end
      
      if params[:center]
        params[:center] = get_coordinates(params[:center])
      end
      
      if params[:map_type]
        case params[:map_type]
        when 'roadmap', :roadmap
          params[:map_type] = 'roadmap'
        when 'mobile', :mobile
          params[:map_type] = 'mobile'
        else
          raise ArgumentError, 'Invalid map type'
        end
      end
      
      if params[:format]
        case params[:format]
        when 'gif', :gif
          params[:format] = 'gif'
        when 'png32', :png32
          params[:format] = 'png32'
        when 'jpg', :jpg
          params[:format] = 'jpg'
        else
          raise ArgumentError, 'Invalid map format'
        end
      end
      
      return params
    end
    
    # Gets serializes coordinates to string from:
    #  Hash: {:lat => 1, :long => 2} => '1,2'
    #  Array: [1, 2] => '1,2'
    #
    # It also checks coordinates are valid
    def self.get_coordinates(coords)
      case coords.class.to_s
      when 'Hash'
        coords[:lat] = coords['lat'] if coords['lat']
        coords[:long] = coords['long'] if coords['long']
        begin
          get_coordinates([coords[:lat], coords[:long]])
        rescue ArgumentError
          raise ArgumentError, "Invalid coordinates: #{coords.inspect}"
        end
      when 'Array'
        begin
          get_coordinates(coords.join(','))
        rescue ArgumentError
          raise ArgumentError, "Invalid coordinates: #{coords.inspect}"
        end
      when 'String'
        if coords =~ /^-?\d+(\.\d+)?,-?\d+(\.\d+)?$/
          coords
        else
          raise ArgumentError, "Invalid coordinates: #{coords.inspect}"
        end
      else
        raise ArgumentError, "Invalid coordinates: #{coords.inspect}"
      end
    end
    
  end
end