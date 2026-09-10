require 'grpc'
require_relative '../../lib/ats_services_pb'

class AtsServer < Ats::AtsService::Service
  def check_recruiter(request, _call)
    # This uses the ActiveRecord Recruiter model!
    is_recruiter = Recruiter.exists?(member_id: request.member_id)
    Ats::CheckRecruiterResponse.new(is_recruiter: is_recruiter)
  end
end
