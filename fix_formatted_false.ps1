# PowerShell script to add formatted="false" to problematic string resources
$file = 'app\src\main\res\values\strings.xml'
$content = Get-Content $file -Raw

# List of string names that need formatted="false"
$names = @(
  'home_adv_room_hint_num',
  'hosting_camera_used',
  'hosting_sensor_used',
  'hs_gateway_armde_failed_ios',
  'hs_gateway_armed_failed_android',
  'hs_hosting_camera_valid',
  'hs_hosting_sensor_valid',
  'hs_message_dispatcher_info',
  'hs_message_receiver_info',
  'hs_smart_gateway_armed_failed_android',
  'hs_smart_gateway_armed_failed_ios',
  'scene_humidity_tip',
  'thing_activator_activate_stop_dialog_content_muti_device',
  'thing_light_scene_batch_delete_toast',
  'ota_batch_upgrade_alert_content',
  'ota_batch_upgrade_alert_unsupport_content',
  'car_alert_tip_description',
  'alexa_link_success'
)

foreach ($name in $names) {
    $pattern = '<string name="' + $name + '">([\s\S]*?)</string>'
    $replace = '<string name="' + $name + '" formatted="false">$1</string>'
    $content = [regex]::Replace($content, $pattern, $replace)
}

Set-Content $file $content -Encoding UTF8
Write-Host "Added formatted=\"false\" to all problematic string resources." 