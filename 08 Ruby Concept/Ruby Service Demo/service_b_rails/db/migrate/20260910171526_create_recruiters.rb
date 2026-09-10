class CreateRecruiters < ActiveRecord::Migration[7.1]
  def change
    create_table :recruiters do |t|
      t.string :member_id
      t.string :name

      t.timestamps
    end
  end
end
