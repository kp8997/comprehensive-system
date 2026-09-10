require "test_helper"

class RecruitersControllerTest < ActionDispatch::IntegrationTest
  test "should get show" do
    get recruiters_show_url
    assert_response :success
  end
end
