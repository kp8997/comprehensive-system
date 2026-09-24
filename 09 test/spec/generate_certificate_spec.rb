require 'rspec'
require 'fileutils'

RSpec.describe 'Certificate Generator CLI' do
  let(:output_filename) { 'certificate.pdf' }
  let(:logo_path) { 'logo.png' }
  let(:backup_logo_path) { 'logo.png.bak' }
  let(:script_path) { 'generate_certificate.rb' }

  # A valid 1x1 pixel transparent PNG byte string
  # This prevents Prawn from crashing with "image file is an unrecognised format"
  let(:valid_png_bytes) { 
    "\x89PNG\r\n\x1A\n\x00\x00\x00\rIHDR\x00\x00\x00\x01\x00\x00\x00\x01\x08\x06\x00\x00\x00\x1F\x15\xC4\x89\x00\x00\x00\x0A\x49\x44\x41\x54\x78\x9C\x63\x00\x01\x00\x00\x05\x00\x01\x0D\x0A\x2D\xB4\x00\x00\x00\x00\x49\x45\x4E\x44\xAE\x42\x60\x82".b 
  }

  before(:all) do
    # Backup user's actual logo before running tests so we don't accidentally delete it!
    FileUtils.mv('logo.png', 'logo.png.bak') if File.exist?('logo.png')
  end

  # Clean up before and after each test
  before(:each) do
    File.delete(output_filename) if File.exist?(output_filename)
  end

  after(:all) do
    File.delete('certificate.pdf') if File.exist?('certificate.pdf')
    File.delete('logo.png') if File.exist?('logo.png')
    
    # Restore user's actual logo
    FileUtils.mv('logo.png.bak', 'logo.png') if File.exist?('logo.png.bak')
  end

  describe 'Happy Paths (Successful Generations)' do
    context 'when the logo is present' do
      before do
        # Write a real valid PNG image instead of FileUtils.touch
        File.binwrite(logo_path, valid_png_bytes)
      end

      it 'generates a PDF using default arguments when no names are provided' do
        result = system("ruby #{script_path}")
        expect(result).to be true
        expect(File.exist?(output_filename)).to be true
        
        # Verify it's actually a PDF file
        content = File.read(output_filename, 5)
        expect(content).to eq("%PDF-")
      end

      it 'generates a PDF using the provided learner name parameter' do
        result = system("ruby #{script_path} 'John User'")
        expect(result).to be true
        expect(File.exist?(output_filename)).to be true
      end

      it 'generates a PDF using both the learner name and course name parameters' do
        result = system("ruby #{script_path} 'John User' 'RSpec Testing 101'")
        expect(result).to be true
        expect(File.exist?(output_filename)).to be true
      end
    end

    context 'when the logo is missing' do
      before do
        File.delete(logo_path) if File.exist?(logo_path)
      end

      it 'gracefully generates a PDF without crashing' do
        result = system("ruby #{script_path} 'No Logo User'")
        expect(result).to be true
        expect(File.exist?(output_filename)).to be true
      end
    end
  end

  describe 'Negative Cases (Handling bad inputs)' do
    it 'handles empty string parameters gracefully without crashing' do
      result = system("ruby #{script_path} '' ''")
      expect(result).to be true
      expect(File.exist?(output_filename)).to be true
    end

    it 'ignores extraneous parameters without failing' do
      result = system("ruby #{script_path} 'Name' 'Course' 'ExtraArgument'")
      expect(result).to be true
      expect(File.exist?(output_filename)).to be true
    end
  end
end
