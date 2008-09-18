class VideosController < ApplicationController
  skip_before_filter :require_login, :only => :index
  
  # GET /videos
  # GET /videos.xml
  def index
    @videos = PendingVideo.find(:all)
    
    # gmap
    @map = GMap.new("map")  
    @map.control_init(:large_map => true, :map_type => true)
    
    points = Array.new
    Video.all.each do |video|
      if video.geotagged?
        points.push([video.lat, video.lng])
        @map.overlay_init(GMarker.new([video.lat, video.lng], {:info_window => video.embed_html(180, 120)}))
      end
    end
    @map.center_zoom_on_points_init(*points)
    
    respond_to do |format|
      format.html # index.html.erb
      format.xml  { render :xml => @videos }
    end
  end

  # GET /videos/1
  # GET /videos/1.xml
  def show
    @video = PendingVideo.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.xml  { render :xml => @video }
    end
  end

  # GET /videos/new
  # GET /videos/new.xml
  def new
    @video = PendingVideo.new

    respond_to do |format|
      format.html # new.html.erb
      format.xml  { render :xml => @video }
    end
  end

  # POST /videos
  # POST /videos.xml
  def create
    if current_user.pending_video
      current_user.pending_video.destroy
    end
    @video = current_user.build_pending_video(params[:video])

    respond_to do |format|
      if @video.save
        logger.debug("#{@video.video.url}")
        flash[:notice] = 'Video was successfully created.'
        format.html { redirect_to(videos_path) }
        format.xml  { render :xml => @video, :status => :created, :location => @video }
      else
        format.html { render :action => "new" }
        format.xml  { render :xml => @video.errors, :status => :unprocessable_entity }
      end
    end
  end

  # PUT /videos/1
  # PUT /videos/1.xml
  def update
    @video = PendingVideo.find(params[:id])

    respond_to do |format|
      if @video.update_attributes(params[:video])
        flash[:notice] = 'Video was successfully updated.'
        format.html { redirect_to(video_path(@video)) }
        format.xml  { head :ok }
      else
        format.html { render :action => "show" }
        format.xml  { render :xml => @video.errors, :status => :unprocessable_entity }
      end
    end
  end

  # DELETE /videos/1
  # DELETE /videos/1.xml
  def destroy
    @video = PendingVideo.find(params[:id])
    @video.upload if params[:upload]
    @video.destroy

    respond_to do |format|
      format.html { redirect_to(videos_url) }
      format.xml  { head :ok }
    end
  end
end
