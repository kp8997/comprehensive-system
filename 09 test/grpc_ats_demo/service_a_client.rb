#!/usr/bin/env ruby

$LOAD_PATH.unshift(File.expand_path('./lib', __dir__))

require 'grpc'
require 'ats_services_pb'

def main
  hostname = 'localhost:50051'
  stub = Ats::AtsService::Stub.new(hostname, :this_channel_is_insecure)
  
  puts "Service A (Main App): Connecting to Service B at #{hostname}..."

  # Check for member_id '123' (which should be true)
  member_id = '123'
  puts "\nService A: Asking Service B if member #{member_id} is a recruiter..."
  response = stub.check_recruiter(Ats::CheckRecruiterRequest.new(member_id: member_id))
  puts "Service B responded: #{response.is_recruiter ? 'YES' : 'NO'} (is_recruiter=#{response.is_recruiter})"
  
  # Check for member_id '999' (which should be false)
  member_id = '999'
  puts "\nService A: Asking Service B if member #{member_id} is a recruiter..."
  response = stub.check_recruiter(Ats::CheckRecruiterRequest.new(member_id: member_id))
  puts "Service B responded: #{response.is_recruiter ? 'YES' : 'NO'} (is_recruiter=#{response.is_recruiter})"
end

main
