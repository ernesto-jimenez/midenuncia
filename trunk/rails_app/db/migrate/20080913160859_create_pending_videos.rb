class CreatePendingVideos < ActiveRecord::Migration
  def self.up
    create_table :pending_videos do |t|
      t.integer :user_id
      t.string :video_file_name
      t.string :video_file_size
      t.string :video_content_type
      t.string :address
      t.string :lat
      t.string :lng
      t.boolean :confirmed, :default => false

      t.timestamps
    end
  end

  def self.down
    drop_table :pending_videos
  end
end
