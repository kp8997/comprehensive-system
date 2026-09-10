require 'grpc'
require_relative '../../lib/ats_services_pb'

class RecruitersController < ApplicationController
  def show
    member_id = params[:id]
    
    # Initialize the gRPC Stub (Client)
    # In a real app, this hostname is stored in ENV['ATS_SERVICE_URL']
    hostname = 'localhost:50051'
    stub = Ats::AtsService::Stub.new(hostname, :this_channel_is_insecure)
    
    # Make the remote procedure call to Service B
    begin
      request = Ats::CheckRecruiterRequest.new(member_id: member_id)
      response = stub.check_recruiter(request)
      
      render json: { 
        member_id: member_id, 
        is_recruiter: response.is_recruiter,
        source: 'gRPC from Service B (Rails ATS)'
      }
    rescue GRPC::BadStatus => e
      render json: { error: "gRPC Error: #{e.message}" }, status: :internal_server_error
    end
  end
end
