Vagrant.configure(2) do |config|
  config.vm.box = "ubuntu/trusty64"
  config.vm.hostname = "hackillinois"

  config.vm.network "forwarded_port", host: 18888, guest: 18888

  config.vm.provider "virtualbox" do |vb|
    vb.name = "hackillinois"
    vb.customize ["modifyvm", :id, "--memory", "2048"]
  end

  config.vm.synced_folder "../login", "/home/vagrant/dev/login"
  config.vm.provision :shell, privileged: false, path: "vagrant-bootstrap.sh"
end
