require 'cgi'
require 'net/http'

module Google
  class StaticBase
    # Builds URL
    def self.build_url(params={})
      query = []
      params.each do |key, value|
        query << "#{CGI.escape(key.to_s)}=#{CGI.escape(value.to_s)}"
      end
      return "#{self::URL}?#{query.join('&')}"
    end
    
    # Returns URL content
    def self.fetch_url(url)
      response = Net::HTTP.get_response(URI.parse(url))
      case response
      when Net::HTTPSuccess     then response.body
      when Net::HTTPRedirection then fetch(response['location'], limit - 1)
      else
        raise ArgumentError, "Couldn't get the image"
      end
    end
    
    def self.const_missing(name)
      if name.to_s == 'URL'
        raise ArgumentError, 'Subclasses must define URL contant'
      else
        super(name)
      end
    end
  end
end