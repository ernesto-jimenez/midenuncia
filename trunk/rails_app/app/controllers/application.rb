# Filters added to this controller apply to all controllers in the application.
# Likewise, all the methods added will be available for all controllers.

class ApplicationController < ActionController::Base
  helper :all # include all helpers, all the time

  # See ActionController::RequestForgeryProtection for details
  # Uncomment the :secret if you're not using the cookie session store
  protect_from_forgery # :secret => '3edfea3a54f5983c65f7d2ae16b2b044'
  
  # See ActionController::Base for details 
  # Uncomment this to filter the contents of submitted sensitive data parameters
  # from your application log (in this case, all fields with names like "password"). 
  # filter_parameter_logging :password
  
  before_filter :require_login
  
private
  def require_login
    if not logged_in?
      session[:forbidden_url] = request.path
      redirect_to login_path
      return false
    end
  end
  
  def logged_in?
    return(not current_user.blank?)
  end
  
  def current_user
    if @current_user.nil?
      @current_user = (session[:user_id].nil? ? nil : User.find_by_id(session[:user_id])) || false
    end
    return @current_user
  end
  helper_method :current_user
  
  
  def maps_api_key
    if request.host_with_port == 'localhost:3000'
    return 'ABQIAAAAnYifODmURuREAUoO1JrZeBTJQa0g3IQ9GZqIMmInSLzwtGDKaBQTTHWC6huQHzLz86x4qAF4u5uA3g'
    elsif request.host_with_port == 'localhost'
      return 'ABQIAAAAnYifODmURuREAUoO1JrZeBT2yXp_ZAY8_ufC3CFXhHIE1NvwkxRAR7xBgj2bU0JF8M8bvdTZEUZg6w'
    elsif request.host_with_port == '208.78.96.249'  
      return 'ABQIAAAAnYifODmURuREAUoO1JrZeBQjrf-VTPmumQvjR-lmU63-nS9zUBQCfivvvnu0AiL3cAyrsN8wk625hg'
    elsif request.host_with_port == '81.46.167.56'
      return 'ABQIAAAAnYifODmURuREAUoO1JrZeBQZijiOHmHV9tsOzSoKpKRRj9r4KhRyrSIDVydN2n13iJVHAQXJoR-SPw'
    elsif request.host_with_port == '83.34.234.6'
      return 'ABQIAAAAnYifODmURuREAUoO1JrZeBRJE4nehXZVbM2MzaIpABch14bPqBSJZK54Xp1mLlmTzxjpKXn595X38Q'
    elsif request.host_with_port == 'salimos.ernesto-jimenez.com'
      return 'ABQIAAAAnYifODmURuREAUoO1JrZeBS7Ykjcc-_h5MhB1uFiMt6mg1oxsxQYbYVrtELeHsgmvkiI716Ergk4mg'
    elsif request.host_with_port == 'midenuncia.ernesto-jimenez.com'
      return 'ABQIAAAAnYifODmURuREAUoO1JrZeBS-hQkDLulnyOjWdlVOXzrGPy3qjhSL9DG31DbSRMnmrEH6ij33R25TfA'
    else
      return ''
    end
  end
  helper_method :maps_api_key
end
