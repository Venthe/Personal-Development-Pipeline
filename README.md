# Personal Development Pipeline

Learn-along exercise in creating enterprise-grade infrastructure.

## Notes

This is an evergreen exercise, it SHOULD NOT be used in production; Moreover licenses for specific software should be followed.

### Prerequisites

Tested on VirtualBox on Windows (@SL2)

#### WSL2

Prepare WSL to work with VirtualBox

```bash
sudo apt install vagrant python python3-pip --assume-yes
pip3 install ansible --user
# Add to bashrc
echo 'PATH=$HOME/.local/bin:$PATH' >> ~/.bashrc
echo 'export VAGRANT_WSL_WINDOWS_ACCESS_USER_HOME_PATH="/mnt/z"' >> ~/.bashrc
echo 'export VAGRANT_WSL_ENABLE_WINDOWS_ACCESS="1"' >> ~/.bashrc
echo 'export VAGRANT_DEFAULT_PROVIDER=hyperv' >> ~/.bashrc
```

## Usage

To create all test deployments
```bash
cd provisioning/cluster_vagrant
vagrant up
```

## Authors

* **Jacek Lipiec** - *Initial work* - [Venthe](https://github.com/Venthe)

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgements

* README based on [PurpleBooth](https://gist.githubusercontent.com/PurpleBooth/109311bb0361f32d87a2/raw/8254b53ab8dcb18afc64287aaddd9e5b6059f880/README-Template.md)