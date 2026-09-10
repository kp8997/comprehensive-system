class RecruitersController < ApplicationController
  def show
    member_id = params[:id]
    
    # Query the same database that the gRPC server uses
    is_recruiter = Recruiter.exists?(member_id: member_id)
    
    render json: { 
      member_id: member_id, 
      is_recruiter: is_recruiter,
      source: 'HTTP Web Request directly to Service B'
    }
  end
end
