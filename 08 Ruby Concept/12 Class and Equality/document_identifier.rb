class DocumentIdentifier
  attr_reader :folder, :name

  def initialize(folder, name)
    @folder = folder
    @name = name
  end

  # By default, == is similar to .equals? for instance, we have to override it if needed
  # without this == between 2 object of DocumentIdentifier will be false
  # because they are 2 different object in memory despite having same value
  def ==(other)
    return true if other.equal?(self)
    return false unless other.instance_of?(self.class)
    folder == other.folder && name == other.name
  end
end

one = DocumentIdentifier.new('secret/plane', 'gun.txt')
two = DocumentIdentifier.new('secret/plane', 'gun.txt')

puts "equal" if one == two

puts one == two
