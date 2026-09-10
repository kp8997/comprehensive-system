Recruiter.find_or_create_by!(member_id: '123', name: 'Alice')
Recruiter.find_or_create_by!(member_id: '456', name: 'Bob')
puts "Seeded Recruiters!"
