# Personal Development Pipeline

Learn-along excercise in creating enterprise-grade infrastructure.

## Notes

This is an evergreen excercise, it SHOULD NOT be used in production; Moreover licenses for specific software shold be followed.

### Prerequisites

```
Tested on Docker for Windows.

- You need to enable Kubernetes
- You need to share drive C: on Kubernetes
- You need to enable docker daemon exposing of TCP

```

```
Tested on microk8s in VM on Ubuntu Server 20.04.

- You need to install Docker
- You need to install microk8s
- Perform following commands:
  sudo swapoff -a
  sudo sysctl -w vm.max_map_count=262144
  echo "vm.max_map_count=262144" | sudo tee --append /etc/sysctl.conf
  sudo usermod -a -G microk8s <username>
  microk8s enable dns

```

## Usage

To create all test deployments
```bash
bash ./bootstrap.sh
```

## Authors

* **Jacek Lipiec** - *Initial work* - [Venthe](https://github.com/Venthe)

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgements

* README based on [PurpleBooth](https://gist.githubusercontent.com/PurpleBooth/109311bb0361f32d87a2/raw/8254b53ab8dcb18afc64287aaddd9e5b6059f880/README-Template.md)