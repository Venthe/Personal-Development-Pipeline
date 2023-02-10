import { shellMany } from '@pipeline/process';

async function run() {
  process.env['DEBIAN_FRONTEND'] = 'noninteractive';
  await shellMany([
    `apt-get -qq install software-properties-common --assume-yes`,
    `add-apt-repository ppa:deadsnakes/ppa`,
    `apt-get -qq update`,
    `apt-get -qq install python3.8 python3-pip --assume-yes`
  ]);
}

run();