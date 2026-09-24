#!/usr/bin/env ruby

$LOAD_PATH.unshift(File.expand_path('./lib', __dir__))

require 'grpc'
require 'ats_services_pb'

class AtsServer < Ats::AtsService::Service
  def check_recruiter(check_recruiter_req, _unused_call)
    member_id = check_recruiter_req.member_id
    puts "Service B (ATS): Received request to check member: #{member_id}"
    
    # Mock logic: member '123' and '456' are recruiters.
    is_recruiter = ['123', '456'].include?(member_id)
    
    Ats::CheckRecruiterResponse.new(is_recruiter: is_recruiter)
  end
end

def main
  port = '0.0.0.0:50051'
  s = GRPC::RpcServer.new
  s.add_http2_port(port, :this_port_is_insecure)
  puts "Service B (ATS) server running insecurely on #{port}"
  s.handle(AtsServer)
  s.run_till_terminated_or_interrupted([1, 'int', 'SIGQUIT'])
end

main
