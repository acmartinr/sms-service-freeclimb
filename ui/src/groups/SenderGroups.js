import React, {Component} from 'react';

import './SenderGroups.css';
import '../common/Common.css'

import CampaignsAPI from '../campaigns/CampaignsAPI';
import Session from '../common/Session';
import RemoveModal from '../common/RemoveModal';

import SenderGroupDetailsModal from './SenderGroupDetailsModal';

import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import TablePagination from '@material-ui/core/TablePagination';
import Edit from '@material-ui/icons/Edit';
import Delete from '@material-ui/icons/Delete';
import Button from '@material-ui/core/Button';

class SenderGroups extends Component {
  constructor(props) {
    super(props);

    this.state = {
      selectedSenderGroup: {},
      senderGroups: [],
      count: 0,
      rowsPerPage: 10,
      page: 0,
      showAddSenderGroupModal: false,
      showRemoveSenderGroupModal: false
    };

    this.handleChangePage = this.handleChangePage.bind(this);
    this.handleChangeRowsPerPage = this.handleChangeRowsPerPage.bind(this);
    this.showAddSenderGroupModal = this.showAddSenderGroupModal.bind(this);
    this.closeEditModal = this.closeEditModal.bind(this);
    this.editSenderGroup = this.editSenderGroup.bind(this);
    this.removeSelectedSenderGroup = this.removeSelectedSenderGroup.bind(this);
    this.closeRemoveModal = this.closeRemoveModal.bind(this);
    this.removeSenderGroup = this.removeSenderGroup.bind(this);
  }

  removeSelectedSenderGroup() {
    CampaignsAPI.removeSenderGroup(this.state.selectedSenderGroup, response => {
      if (response.status === 'OK') {
        this.closeRemoveModal();
        this.updateSenderGroups();
      }
    });
  }

  removeSenderGroup(senderGroup) {
    this.setState({
      selectedSenderGroup: senderGroup,
      showRemoveSenderGroupModal: true,
      title: 'Remove sender group',
      message: 'Are you sure you want to remove this sender group?'})
  }

  closeRemoveModal(senderGroup) {
    this.setState({selectedSenderGroup: {}, showRemoveSenderGroupModal: false})
  }

  editSenderGroup(senderGroup) {
    this.setState({selectedSenderGroup: senderGroup, showAddSenderGroupModal: true})
  }

  showAddSenderGroupModal() {
    this.setState({showAddSenderGroupModal: true});
  }

  closeEditModal() {
    this.setState({selectedSenderGroup: {}, showAddSenderGroupModal: false, page: 0});
    this.updateSenderGroups();
  }

  componentDidMount() {
    this.updateSenderGroups();
  }

  handleChangePage(event, value) {
    this.setState({page: value}, this.updateSenders);
  }

  handleChangeRowsPerPage(event) {
    this.setState({rowsPerPage: event.target.value}, this.updateSenders);
  }

  prettyDate(dateMS) {
    if (dateMS > 0) {
      const date = new Date(dateMS);
      return date.toLocaleDateString(date) + ' ' + date.toLocaleTimeString(date);
    } else {
      return 'empty';
    }
  }

  updateSenderGroups() {
    const request = {
      userId: Session.getUser().id,
      page: this.state.page,
      limit: this.state.rowsPerPage,
      search: this.state.search
    };

    CampaignsAPI.getSenderGroups(request,
    response => {
      if (response.status === "OK") {
        this.setState({
          senderGroups: response.data.groups,
          count: response.data.count
        });
      }
    });
  }

  render() {
    return (
      <div className='max-width-800'>
        <Button
          type="button"
          variant="contained"
          color="primary"
          onClick={(e) => this.showAddSenderGroupModal()}
          className="right">Add New Sender Group</Button>

        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Name</TableCell>
              <TableCell>Count</TableCell>
              <TableCell>Date</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {this.state.senderGroups.map(row => (
              <TableRow key={row.id}>
                <TableCell>{row.name}</TableCell>
                <TableCell>{row.sendersCount}</TableCell>
                <TableCell>{this.prettyDate(row.date)}</TableCell>
                <TableCell className='action-cell'>
                  <Edit
                    className='pointer right-margin-10'
                    color='primary'
                    onClick={(e) => this.editSenderGroup(row)}/>
                  <Delete
                    className='pointer'
                    color='primary'
                    onClick={(e) => this.removeSenderGroup(row)}/>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>

        <TablePagination
          rowsPerPageOptions={[10, 25, 50]}
          component="div"
          count={this.state.count}
          rowsPerPage={this.state.rowsPerPage}
          page={this.state.page}
          backIconButtonProps={{
            'aria-label': 'Previous Page',
          }}
          nextIconButtonProps={{
            'aria-label': 'Next Page',
          }}
          onChangePage={this.handleChangePage}
          onChangeRowsPerPage={this.handleChangeRowsPerPage}
        />

        {this.state.showAddSenderGroupModal &&
          <SenderGroupDetailsModal
            selectedSenderGroup={this.state.selectedSenderGroup}
            handleClose={this.closeEditModal} />
        }

        {this.state.showRemoveSenderGroupModal &&
          <RemoveModal
            title={this.state.title}
            message={this.state.message}
            remove={this.removeSelectedSenderGroup}
            close={this.closeRemoveModal} />
        }
      </div>
    )
  }
}

export default SenderGroups;
