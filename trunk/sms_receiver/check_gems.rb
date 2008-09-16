GEMS = ['mechanize','gmailer','json','hpricot','hoe','xml-simple', 'daemons']

def try_lib(lib, print=true)
  print "Checking #{lib}: " if print
  begin
    require lib
  rescue Exception
    puts "FAIL" if print
    return false
  end
  puts "OK" if print
  return true
end

unless try_lib('rubygems')
  STDERR.puts "You need to install rubygems.\ncheck http://docs.rubygems.org/"
  exit(false)
end

pending_to_install = []
GEMS.each do |gem|
  pending_to_install << gem unless try_lib(gem)
end

puts ""
if pending_to_install.empty?
  puts "Everything OK. Your environment is ready"
else
  STDERR.puts "You need to install some gems, type in your terminal:"
  STDERR.puts "sudo gem install #{pending_to_install.join(' ')}"
end