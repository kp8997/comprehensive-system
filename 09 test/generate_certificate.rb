#!/usr/bin/env ruby

# We use bundler/inline to automatically install the required 'prawn' gem 
# if it's not already installed on your system. This makes the script ready to run!
require 'bundler/inline'

gemfile do
  source 'https://rubygems.org'
  gem 'prawn'
end

require 'prawn'

def generate_certificate(learner_name, course_name, logo_path, output_filename)
  # Create a landscape A4 document
  Prawn::Document.generate(output_filename, page_layout: :landscape, page_size: 'A4') do |pdf|
    
    # Draw a nice decorative border around the page
    pdf.stroke_color 'DDDDDD'
    pdf.line_width 4
    pdf.stroke_bounds
    
    # Reset color for text
    pdf.fill_color '000000'

    # 1. Add Company Logo
    # We check if the logo file exists to prevent the script from crashing if it's missing
    if File.exist?(logo_path)
      # Position the logo at the top center
      pdf.image logo_path, width: 120, position: :center, vposition: 30
    else
      pdf.move_down 60
      pdf.text "[Company Logo Missing - Please save your logo as '#{logo_path}']", align: :center, size: 14, color: "999999"
    end

    pdf.move_down 50

    # 2. Main Title
    pdf.font "Helvetica", style: :bold
    pdf.text "Certificate of Completion", align: :center, size: 42, color: "333333"
    
    pdf.move_down 30
    
    # 3. Presenter Text
    pdf.font "Helvetica", style: :normal
    pdf.text "This is to proudly certify that", align: :center, size: 18, color: "666666"
    
    pdf.move_down 20
    
    # 4. Learner Name
    pdf.font "Helvetica", style: :bold
    pdf.text learner_name, align: :center, size: 32, color: "000000"
    
    pdf.move_down 20
    
    # 5. Course Completion Text
    pdf.font "Helvetica", style: :normal
    pdf.text "has successfully completed the course", align: :center, size: 18, color: "666666"
    
    pdf.move_down 20
    
    # 6. Course Name
    pdf.font "Helvetica", style: :bold
    pdf.text course_name, align: :center, size: 28, color: "2C3E50"
    
    pdf.move_down 40
    
    # 7. Congratulatory Message
    pdf.font "Helvetica", style: :italic
    pdf.text "Congratulations on your outstanding achievement!", align: :center, size: 16, color: "444444"
    
    pdf.move_down 35
    
    # 8. Date and Signature Fields at the bottom
    pdf.font "Helvetica", style: :normal
    
    # Use the current cursor position as the top of the signature boxes
    signature_y_position = pdf.cursor
    
    # Date Box (Left side)
    pdf.bounding_box([pdf.bounds.left + 80, signature_y_position], width: 200, height: 40) do
      pdf.stroke_color '000000'
      pdf.line_width 1
      pdf.stroke_horizontal_rule
      pdf.move_down 10
      pdf.text "Date", align: :center, size: 14
      # Draw today's date above the line
      pdf.move_up 35
      pdf.text Time.now.strftime("%B %d, %Y"), align: :center, size: 14
    end

    # Signature Box (Right side)
    pdf.bounding_box([pdf.bounds.right - 280, signature_y_position], width: 200, height: 40) do
      pdf.stroke_color '000000'
      pdf.line_width 1
      pdf.stroke_horizontal_rule
      pdf.move_down 10
      pdf.text "Instructor Signature", align: :center, size: 14
    end
  end
end

if __FILE__ == $0
  # Configuration
  # ARGV[0] gets the first argument passed from the command line for the learner's name.
  # ARGV[1] gets the second argument passed from the command line for the course name.
  # If they are empty, they fall back to the default values below.
  learner_name = ARGV[0] || "Jane Doe"
  course_name = ARGV[1] || "Advanced Ruby Programming"
  logo_path = "logo.png" # Make sure to save the image you provided as 'logo.png' in this directory
  output_filename = "certificate.pdf"

  puts "Generating certificate for #{learner_name}..."

  begin
    generate_certificate(learner_name, course_name, logo_path, output_filename)
    puts "✅ Success! Certificate generated: #{output_filename}"
  rescue StandardError => e
    puts "❌ Error generating certificate: #{e.message}"
  end
end
