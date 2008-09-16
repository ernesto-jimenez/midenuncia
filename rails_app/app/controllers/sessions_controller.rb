class SessionsController < ApplicationController
  skip_before_filter :require_login
  # GET /sessions/new
  # GET /sessions/new.xml
  def new
    respond_to do |format|
      format.html # new.html.erb
    end
  end
  
  def send_code
    params[:phone] = params[:phone].to_i
    @user = User.find_or_create_by_phone(params[:phone])
    
    @user.send_new_code
    
    render :update do |page|
      if @user.valid?
        page.replace :login, :partial => 'extend_login'
        page.call '$("code").focus'
      else
        flash[:error] = 'Invalid phone.'
        page.redirect_to login_path
      end
    end
  end
  
  # POST /sessions
  # POST /sessions.xml
  def create
    params[:phone] = params[:phone].to_i
    params[:code] = params[:code].to_i
    @current_user = User.find_by_phone_and_code(params[:phone], params[:code])

    respond_to do |format|
      if current_user
        session[:user_id] = current_user.id
        cookies[:phone] = params[:phone].to_s
        flash[:notice] = 'Session was successfully created.'
        format.html { redirect_to(session[:forbidden_url] || home_path) }
      else
        flash[:error] = 'Invalid code, try again.'
        format.html { render :action => "new" }
      end
    end
  end
  # DELETE /sessions/1
  # DELETE /sessions/1.xml
  def destroy
    @current_user = nil
    session[:user_id] = nil
    
    respond_to do |format|
      format.html { redirect_to(home_path) }
    end
  end
end
