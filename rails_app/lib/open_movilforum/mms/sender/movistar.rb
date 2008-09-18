require 'digest/md5'
require 'mechanize'
require 'open_movilforum/mms/image_download'

## ATENCIÓN
# MOVISTAR CAMBIÓ SU WEB A MITAD DEL CONCURSO Y LA API DE OPEN MOVIL FORUM YA NO
# FUNCIONA.
# NO HEMOS PODIDO REFACTORIZAR ESTE CÓDIGO PORQUE LA APLICACIÓN NOS DABA MUCHOS
# ERRORES 500, ASÍ QUE HEMOS TENIDO QUE DEJAR EL CÓDIGO CUTRE Y SUCIO QUE SALIÓ
# DE DARSE DE CABEZAZOS CONTRA LA WEB

module OpenMovilforum
  module MMS
    module Sender
      # Movistar MMS Sender
      class Movistar
        LOGIN = {
          :user => TELEFONO_ENVIO,
          :pass => '1509',
        }
        
        def send(msg)
            @msg = msg            
            
            puts "Login @ movistar..."
            self.login
            puts "Building message..."
            self.build
            puts "Sending message..."
            
            form = @page.forms.name("mmsForm").first
            @page = @agent.submit(form)
            
            #debugger
            
            puts "Message sent"
        end
        
        def login
          # step 1. Go to the login page
          uri = URI.parse("http://www.multimedia.movistar.es/authenticate")
          @agent = WWW::Mechanize.new
          @agent.user_agent_alias = "Windows IE 7"
          @page = @agent.get(uri)
          
          # step 2. Fill the form
          form = @page.forms.name("loginForm").first
          form.TM_LOGIN = LOGIN[:user]
          form.TM_PASSWORD = LOGIN[:pass]
          @page = @agent.submit(form)        
          
          # step 3. Create a new message
          @page = @agent.get("/do/multimedia/create?l=sp-SP&v=mensajeria")
        end
        
        def build()   
          # fill text fields
          
          page = @agent.get("/do/multimedia/upload?l=sp-SP&v=mensajeria")
          form_upload = page.forms.name('mmsComposerUploadItemForm').first
          form_upload.file_uploads.first.file_name = @msg.attach
          
          result = @agent.submit(form_upload)
          
          form = @page.forms.name("mmsForm").first
          @page = @agent.submit(form)
          form = @page.forms.name("mmsForm").first
          form.to = @msg.destination   
          form.subject = @msg.subject
          form.text = @msg.text
          form.action = "/do/multimedia/send?l=sp-SP&v=mensajeria"
        end
        
        # send the message with a video attached
        def self.send(to, subject, attach, body)
            # Build message object
            msg = Message.new(to, subject, attach, body)            
            movi = Movistar.new().send(msg)
        end
        
      end
    end
  end
end